###################
# BUILDER
###################

FROM node:latest AS node-builder

RUN mkdir -p /fe_build

WORKDIR /fe_build

COPY ./ /fe_build

RUN apt-get update && apt-get install -y default-jre

RUN yarn install

RUN yarn shadow-cljs release frontend

# FROM clojure:temurin-20-tools-deps-jammy AS clj-builder

# RUN mkdir -p /build
# WORKDIR /build
# COPY THAT JS SHIT TO THE NEW BUILD DEALIO
# COPY deps.edn /build/
# 
# RUN the yarn build to be honest
# 
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
