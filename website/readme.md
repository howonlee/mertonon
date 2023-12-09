Mertonon Website
----

[mertonon.com](https://mertonon.com) is the Mertonon website. We're monorepoing it, so here's how to build the thing

If you get blocked by github api rate limit, figure out the github api authn. Authenticated limit is 1000/hour, unauthenticated is 60/hour. We use github api to figure out which zip download is the latest, in a way that's completely decoupled from the rest of the repo

1. Install babashka, aws-cli
2. Have the proper AWS credentials in with aws-cli (not stored here, being public)
3. Run `bb site.clj` which will mutate stuff in `to_upload`
4. Upload to s3 with `upload.sh`
5. Maybe proc the cache invalidation on Cloudfront
6. Look at result on website
