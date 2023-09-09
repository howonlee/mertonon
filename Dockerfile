###
# BUILDER
###

# FE build

FROM node:latest AS node-builder

RUN mkdir -p /fe_build

WORKDIR /fe_build

COPY ./ /fe_build

RUN apt-get update && apt-get install -y default-jre

RUN yarn install

RUN yarn shadow-cljs release frontend

# BE build

FROM clojure:temurin-20-tools-deps-jammy AS clj-builder

RUN mkdir -p /be_build
WORKDIR /be_build
COPY ./ /be_build

# copy from FE

# RUN clojure -T:build uberjar

###################
# RUNNER
###################

# FROM scratch AS runner
#
# EXPOSE 5036
#
## envvars...
#
# ENTRYPOINT ["/some crap"]
