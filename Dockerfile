# This is the dockerfile for building then testing Mertonon only
# So, for the Mertonon maintainers, not for non-weirdoes trying to use Mertonon
# We will have a dockerfile for getting and running Mertonon eventually

FROM clojure:temurin-17-alpine AS builder
ENV CLOJURE_VERSION=1.11.1.1182
RUN mkdir -p /build
WORKDIR /build

COPY deps.edn /build/
RUN clojure -P -X:build

## yarn release somewhere here...

COPY ./ /build
RUN clojure -T:build uberjar
