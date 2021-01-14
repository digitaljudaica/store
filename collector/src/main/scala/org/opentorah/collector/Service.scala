package org.opentorah.collector

import cats.effect.{Blocker, ContextShift, ExitCode, Sync}
//import cats.implicits._
//import fs2.io
import java.net.URL
import java.util.concurrent.Executors
import net.logstash.logback.argument.StructuredArguments
import org.http4s.{HttpRoutes, Request, Response, StaticFile, Status, Uri}
import org.http4s.dsl.Http4sDsl
import org.http4s.implicits._
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.util.CaseInsensitiveString
import org.slf4j.{Logger, LoggerFactory}
import scala.concurrent.{ExecutionContext, ExecutionContextExecutor}
import zio.{App, RIO, URIO, ZEnv, ZIO}
import zio.duration.Duration
import zio.interop.catz._

object Service extends App {
  type ServiceEnvironment = ZEnv

  type ServiceTask[+A] = RIO[ServiceEnvironment, A]

  // TODO HTTP GET from http://metadata.google.internal/computeMetadata/v1/project/project-id
  // with `Metadata-Flavor: Google` header;
  // or do I even need it for logging?
  private val projectId: String = "alter-rebbe-2"

  val bucketName: String = "store.alter-rebbe.org"

  val dsl: Http4sDsl[ServiceTask] = Http4sDsl[ServiceTask]
  import dsl._

  LoggerFactory.getILoggerFactory.asInstanceOf[ch.qos.logback.classic.LoggerContext]
    .getLogger(Logger.ROOT_LOGGER_NAME).setLevel(ch.qos.logback.classic.Level.INFO)

  override def run(args: List[String]): URIO[ServiceEnvironment, zio.ExitCode] = {
    val siteUri: String = if (args.nonEmpty) {
      val result = args.head
      info(s"siteUri argument supplied: $result")
      result
    } else getParameter("STORE", s"http://$bucketName/")

    val port: Int = getParameter("PORT", "4000").toInt

    val executionContext: ExecutionContextExecutor = ExecutionContext.global

    // This is supposed to be set when running in Cloud Run
    val serviceName: Option[String] = Option(System.getenv("K_SERVICE"))
    warning(s"serviceName=$serviceName")

    ZIO.runtime[ServiceEnvironment].flatMap { implicit rts =>
      BlazeServerBuilder[ServiceTask](executionContext)
        // To be accessible when running in a docker container the server
        // must bind to all IPs, not just 127.0.0.1:
        .bindHttp(port, "0.0.0.0")
        .withWebSockets(false)
        .withHttpApp(routes(Uri.unsafeFromString(siteUri)).orNotFound)
        .serve
        .compile[ServiceTask, ServiceTask, ExitCode]
        .drain
    }
      .mapError(err => zio.console.putStrLn(s"Execution failed with: $err"))
      .exitCode
  }

  private def getParameter(name: String, defaultValue: String): String =Option(System.getenv(name)).fold {
    info(s"No value for '$name' in the environment; using default: '$defaultValue'")
    defaultValue
  }{ value =>
    info(s"Value    for '$name' in the environment: $value")
    value
  }

  val blocker: Blocker = Blocker.liftExecutorService(Executors.newFixedThreadPool(2))

  private def routes(siteUri: Uri): HttpRoutes[ServiceTask] = {
//    val site: Site = new Site(toUrl(siteUri))

    HttpRoutes.of[ServiceTask] {
      // case GET -> Root / "hello" => Ok("hello!")

      case request@GET -> _ =>
        fromUrl(toUrl(siteUri.resolve(relativize(addIndex(request.uri)))), request)
    }
  }

  private def addIndex(uri: Uri): Uri =
    if (uri.path.endsWith("/")) uri.copy(path = uri.path + "index.html") else uri

  private def relativize(uri: Uri): Uri =
    if (uri.path.startsWith("/")) uri.copy(path = uri.path.substring(1)) else uri

  private def toUrl(uri: Uri): URL = new URL(uri.toString)

  def fromUrl(url: URL, request: Request[ServiceTask]): ServiceTask[Response[ServiceTask]] =
    X.fromUrl(url, request)

//  private val defaultBufferSize: Int = 10240

//  def getUrl[F[_]](url: URL)(implicit F: Sync[F], cs: ContextShift[F]): F[Option[fs2.Stream[F, Byte]]] = {
//    blocker
//      .delay(url.openConnection.getInputStream)
//      .redeem(
//        recover = {
//          case _: java.io.FileNotFoundException => None
//          case other => throw other
//        },
//        f = { inputStream =>
//          Some(io.readInputStream[F](F.pure(inputStream), defaultBufferSize, blocker))
//        }
//      )
//  }

  def log(
    url: URL,
    request: Request[ServiceTask],
    duration: Duration,
    status: Status
  ): Unit = {
    val durationStr: String = formatDuration(duration)

    // TODO suppress URL encoding.
    val urlStr: String = url.toString
    if (status.isInstanceOf[NotFound.type])
      warning(request, s"NOT $durationStr $urlStr")
    else
      info   (request, s"GOT $durationStr $urlStr")
  }

  private def formatDuration(duration: Duration): String = {
    val millis: Long = duration.toMillis
    if (millis < 1000) s"$millis ms" else {
      val seconds: Float = Math.round(millis.toFloat/100).toFloat/10
      s"$seconds s"
    }
  }

  private def info   (request: Request[ServiceTask], message: String): Unit = log(Some(request), message, "INFO"   )
  private def info   (                               message: String): Unit = log(None         , message, "INFO"   )
//  private def notice (request: Request[ServiceTask], message: String): Unit = log(Some(request), message, "NOTICE" )
//  private def notice (                               message: String): Unit = log(None         , message, "NOTICE" )
  private def warning(request: Request[ServiceTask], message: String): Unit = log(Some(request), message, "WARNING")
  private def warning(                               message: String): Unit = log(None         , message, "WARNING")

  private val logger: Logger = LoggerFactory.getLogger("org.opentorah.collector.service.Service")

  private def log(request: Option[Request[ServiceTask]], message: String, severity: String): Unit = {
    val trace: String = request
      .flatMap(_
        .headers.get(CaseInsensitiveString("X-Cloud-Trace-Context"))
        .map(_.value.split("/")(0))
      )
      .getOrElse("no-trace")

    logger.info(
      message,
      StructuredArguments.keyValue("severity", severity),
      StructuredArguments.keyValue("logging.googleapis.com/trace", s"projects/$projectId/traces/$trace"),
      null
    )
  }
}

// TODO figure out why doesn't this compile when I unfold it into Service - and do it!
// Cannot convert from String to an Entity, because no
//   EntityEncoder[[+A]zio.ZIO[zio.Has[zio.clock.Clock.Service]
//   with zio.Has[zio.console.Console.Service]
//   with zio.Has[zio.system.System.Service]
//   with zio.Has[zio.random.Random.Service]
//   with zio.Has[zio.blocking.Blocking.Service],Throwable,A], String] instance could be found.
//
//  not enough arguments for method apply:
//  (
//    implicit F: cats.Applicative[
//      [+A]zio.ZIO[zio.Has[Clock+Console+System+Random+Blocking], Throwable, A]
//    ],
//    implicit w: org.http4s.EntityEncoder[
//      [+A]zio.ZIO[zio.Has[Clock+Console+System+Random+Blocking], Throwable, A],
//      String
//    ]
//  )zio.ZIO[
//    zio.Has[Clock+Console+System+Random+Blocking],
//    Throwable,
//    org.http4s.Response[
//      [+A]zio.ZIO[zio.Has[Clock+Console+System+Random+Blocking], Throwable,A]
//    ]
//  ]
//  in trait EntityResponseGenerator.
//Unspecified value parameter w.
//      .getOrElseF(NotFound(s"Not found: $url"))
private object X {
  import Service.ServiceTask
  import Service.dsl._

  def fromUrl(url: URL, request: Request[ServiceTask]): ServiceTask[Response[ServiceTask]] =
    StaticFile
      .fromURL[ServiceTask](url, Service.blocker, Some(request))
      .getOrElseF(NotFound(s"Not found: $url"))
      .timed.mapEffect {
      case (duration: Duration, response: Response[ServiceTask]) =>
        Service.log(url, request, duration, response.status)
        response
    }
}
