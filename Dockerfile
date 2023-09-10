###
# BUILDER
###

# FE build

FROM node:latest AS node-builder

RUN mkdir -p /fe_build
WORKDIR /fe_build
COPY package.json /fe_build
COPY shadow-cljs.edn /fe_build
COPY yarn.lock /fe_build
COPY src /fe_build/src

RUN apt-get update && apt-get install -y default-jre

RUN yarn install

RUN yarn shadow-cljs release frontend

# BE build

FROM clojure:temurin-20-tools-deps-jammy AS clj-builder

RUN mkdir -p /be_build/resources/public/cljs
WORKDIR /be_build
COPY deps.edn /be_build
COPY build.clj /be_build
# The name of the jar file has number of git commits in it, which is why
COPY .git /be_build/.git
COPY test /be_build/test
COPY resources /be_build/resources
COPY src /be_build/src

COPY --from=node-builder /fe_build/resources/public/cljs /be_build/resources/public/cljs

# Needs a Postgres instance in the host env...
# TODO: dont do this lol

ENV MT_DB_HOST=host.docker.internal

RUN clojure -T:build-ce uberjar

COPY target/*mertonon-prealpha-standalone.jar /be_build/curr-standalone.jar

###
# RUNNER
###

EXPOSE 5036

ENTRYPOINT ["java", "-jar", "curr-standalone.jar"]
