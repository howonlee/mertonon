###################
# BUILDER
###################

FROM clojure:temurin-20-tools-deps-jammy AS builder
RUN mkdir -p /build
WORKDIR /build

COPY deps.edn /build/
## yarn release somewhere here...
RUN the yarn build to be honest

# 
# COPY ./ /build
RUN clojure -T:build uberjar

###################
# RUNNER
###################

# expose our default runtime port
EXPOSE 5036

# run it
ENTRYPOINT ["/some crap"]
