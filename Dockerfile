###################
# BUILDER
###################

FROM clojure:temurin-20-tools-deps-jammy AS builder
RUN mkdir -p /build
WORKDIR /build
COPY deps.edn /build/

## yarn install somewhere here
## yarn build
## yarn release somewhere here...
RUN the yarn build to be honest

RUN clojure -T:build uberjar

###################
# RUNNER
###################

EXPOSE 5036

## envvars...

ENTRYPOINT ["/some crap"]
