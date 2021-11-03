## Static / Dynamic ##

We are moving towards making the site dynamic, so that we can add:  
- search functionality;
- ability to modify the site.

## Project Setup ##

- activated free Google Cloud Platform trial for `dub@opentorah.org` account (6/14/2020); 
- logged into it and created project `alter-rebbe` with id `alter-rebbe-2`
  (id `alter-rebbe` was taken by the previous incarnation,
  and can't be reused even now, long after it was deleted :();
- organization `opentorah.org` was auto-created; 

To set `dub@opentorah.org` as a default account and `alter-rebbe-2` as a default project:
```shell
  $ gcloud auth login dub@opentorah.org
  $ gcloud config set account dub@opentorah.org
  $ gcloud config set project alter-rebbe-2
```

To set Application Default Credentials that Cloud Code in the IDE needs:
```shell
  $ gcloud auth login --update-adc
```

## Store ##

On 2020-12-06: store data is now served from a Google Storage Bucket (and not from GitHub Pages):
- created `store.alter-rebbe.org` bucket;
- made it public;
- pointed CNAME record at it

Code in the `collector` module is called from the `alter-rebbe` project to pre-generate the static store.alter-rebbe.org
site (which it then syncs into the Google Cloud Storage bucket).

## Facsimiles ##

- created bucket `facsimiles.alter-rebbe.org` with:
  - standard storage type;
  - multi-region;
  - uniform access control;
- made it public: in its `Permissions | Add members | New members allUsers`,
 `Select a role | Cloud Storage | Storage Object Viewer`;
- added CNAME record for `facsimiles.alter-rebbe.org` pointing to `c.storage.googleapis.com`;

Added `404.html` and set it as the error page in the website configuration of the bucket
(default error page is in XML).

Facsimiles displayed on the site come from that bucket;
they can be retrieved by anyone who has the correct URL.

Note: Chrome [tightened the nuts on the mixed content](https://blog.chromium.org/2019/10/no-more-mixed-messages-about-https.html),
so links to individual photograph in the facsimile page have to use HTTPS now.
For the SSL certificate's common name to be correct, those links have to point to the photographs indirectly
via `https://storage.googleapis.com/`. So, for example,
`http://facsimiles.alter-rebbe.org/facsimiles/derzhavin6/390.jpg` becomes
`https://storage.googleapis.com/facsimiles.alter-rebbe.org/facsimiles/derzhavin6/390.jpg`.
Alternatively, I can configure an CDN for the facsimiles - but I do not see the need at this point.

Note: I used BFG Repocleaner to remove facsimiles and their Git history from this repository
once they moved out; current tool for things like that is `git-filter-repo`.
   
### Image Processing Commands ###

To extract images from PDF files:
```shell
  $ ls -1 *.pdf | xargs -I {} pdfimages -j {} {}
  $ rename '.pdf-000' '' *
```

To cut facsimiles:
```shell
  $ convert xxx.jpeg -crop 2x1+120@ x-%d.jpeg
```

To compress facsimiles:
```shell
  $ mogrify -path out -quality 80% -format jpg *.tif
```

To sync with the bucket:
```shell
  $ gsutil -m rsync -r -d <path-to-local-copy-of-the-bucket> gs://facsimiles.alter-rebbe.org
```

## JIB ##

Everything is Dockerized nowadays.

I store my Docker image (`gcr.io/alter-rebbe-2/collector`) in the GCP's Container Registry:
```shell
  $ gcloud auth configure-docker
  $ gcloud components install docker-credential-gcr
  $ gcloud services enable containerregistry.googleapis.com
```

I use [jib](https://github.com/GoogleContainerTools/jib) Gradle Plugin to
build and push my Docker image.
Image layers are in the `artifacts.alter-rebbe-2.appspot.com/containers/images` bucket that
was auto-created (with fine-grained access control).

In December 2020 I wrote a [Cloud Run Plugin](https://github.com/dubinsky/cloud-run)
and use it to deploy the service to Cloud Run or run it locally.
Service configuration is in the `service.yaml` file.

Turns out, for ZIO effects and unsafeRuns to work in the CPU-constrained environment,
care needs to be taken with the thread-pools.

## Cloud Run ##

I use [Cloud Run](https://cloud.google.com/run#key-features)
([Unofficial FAQ](https://github.com/ahmetb/cloud-run-faq)).

```shell
 $ gcloud services enable run.googleapis.com
 $ gcloud config set run/platform managed
 $ gcloud config set run/region us-east4
```

Historically:

To add a domain mapping:
```shell
  $ gcloud beta run domain-mappings create
    --service collector
    --domain app.alter-rebbe.org
    --force-override # because of the old project
```

Certificate provisioning spinner starts spinning once DNS record (`app CNAME ghs.googlehosted.com.`)
is in place; mine was there from the previous incarnation. I was getting “unexpectedly closed the
connection” while it was spinning and for a few minutes after it stopped; http 302-redirects to https.

I pointed `www.alter-rebbe.org` at the dynamic app, and configured it to proxy for
`store.alter-rebbe.org` on 2020-07-19.

I do not see the need to set up [Cloud Build](https://cloud.google.com/cloud-build),
but if I do - it runs locally too!

## Service Account ##

Created service account `cloud-run-deploy` and its key:
```shell
  $ gcloud iam service-accounts create cloud-run-deploy
  $ gcloud iam service-accounts keys create ./key.json --iam-account cloud-run-deploy@alter-rebbe-2.iam.gserviceaccount.com
```

For local use, added the key to `~/.gradle/gradle.properties` as a `gcloudServiceAccountKey` property,
with a backslash after each line of the key except the last one, and with backslash-n replaced with backslash-backslash-n :)
(Lost the key during switch to desktop in October 2021; generated a new one.)

Granted the service account the roles for the Container Registry and Cloud Run
(see https://cloud.google.com/run/docs/reference/iam/roles#gcloud):
```shell
  $ gcloud projects add-iam-policy-binding alter-rebbe-2 \
    --member "serviceAccount:cloud-run-deploy@alter-rebbe-2.iam.gserviceaccount.com" --role "roles/storage.admin"
  $ gcloud projects add-iam-policy-binding alter-rebbe-2 \
    --member "serviceAccount:cloud-run-deploy@alter-rebbe-2.iam.gserviceaccount.com" --role "roles/run.admin"
  $ gcloud iam service-accounts add-iam-policy-binding 161107830568-compute@developer.gserviceaccount.com \
    --member="serviceAccount:cloud-run-deploy@alter-rebbe-2.iam.gserviceaccount.com" --role="roles/iam.serviceAccountUser"
```

Granted the user account the role needed for service account impersonation:
```shell
  $ gcloud projects add-iam-policy-binding alter-rebbe-2 \
    --member "user:dub@opentorah.org" --role "roles/iam.serviceAccountTokenCreator"
```

Logged into the service account:
```shell
  $ gcloud auth activate-service-account --key-file=/home/dub/.gradle/gcloudServiceAccountKey-alter-rebbe-2.json
```

To set it as a default account:
```shell
  $ gcloud config set account cloud-run-deploy@alter-rebbe-2.iam.gserviceaccount.com
```

### Logging ###

Excellent [post](https://medium.com/google-cloud/java-logging-on-cloud-run-with-stackdriver-9786d6fdbe17)
by [Averi Kitsch](https://medium.com/@averikitsch)
and the official [documentation](https://cloud.google.com/run/docs/logging#run_manual_logging-java)
on logging in Google Cloud Run were useful.

Special fields in JSON structured payloads are described in the Management Tools logging
[documentation](https://cloud.google.com/logging/docs/agent/configuration#special-fields).

Field `logging.googleapis.com/trace` can be used to nest log entries for the same request
under the request's entry.

### Keeping the service warm ###

```shell
  $ gcloud services enable cloudscheduler.googleapis.com
  $ gcloud scheduler jobs create http collector-service-warmer
      --description "Keep the Collector Service warm"
      --schedule="every 1 hours"
      --http-method GET
      --uri="https://app.alter-rebbe.org"
```

Before the first job is created, service has to be initialized. I did it via Console
(in the same region - us-east4 - my service is running). This created an empty App Engine
application `alter-rebbe-2.appspot.com` and an empty bucket with the same name...

gcloud does not require time zone, but the Console does...

In October 2020, `--min-instances` option to gcloud run deploy became available (in beta);
in November 2020, I switched to using it (I estimate under $10 a month for one kept-warm instance).
If this works out, I won't need the CRON job anymore.


## TODO ##
- [ ] review Cloud Run [features](https://cloud.google.com/blog/products/serverless/looking-back-on-cloud-runs-first-year-since-ga) 
- [ ] run as a service account: https://cloud.google.com/run/docs/configuring/service-accounts  
- [ ] use [API Gateway](https://cloud.google.com/blog/products/serverless/google-cloud-api-gateway-is-now-available-in-public-beta)
  to glue buckets and services together by URL mapping. etc.
- [ ] Memorystore/Redis turned out to be too expensive (Google charges for *provisioned* capacity),
  so the fact that Cloud Run probably can't talk to it even now (4/2020) isn't important :(
- [ ] I may end up using [Cloud Firestore](__https://firebase.google.com/docs/firestore__) for caching generated
  (and maybe even source) files.
- [ ] does [Config Connector](https://cloud.google.com/config-connector/docs/reference/overview)
  work with Cloud Run? Should I use it?
- [ ] use [Tips](https://cloud.google.com/run/docs/tips/java) to configure memory size;
  I see Cache evictions under memory pressure locally and do not see OOM even with 128MB;
  lemme configure the thing in Cloud Run with 256 and see if it holds up...
- [ ] update this write-up;  
  