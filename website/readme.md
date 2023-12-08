Mertonon Website
----

[mertonon.com](https://mertonon.com) is the Mertonon website. We're monorepoing it, so here's how to build the thing

1. Install babashka, aws-cli
2. Have the proper AWS credentials in with aws-cli (not stored here, being public)
3. Run `bb site.clj` which will mutate stuff in `to_upload`
4. Upload to s3 with `upload.sh`
5. Maybe proc the cache clearance on Cloudfront
6. Look at result on website
