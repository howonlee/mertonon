###################
# BUILDER
###################

FROM clojure:temurin-20-tools-deps-jammy AS clj-builder

# apt-get install node shadow cljs and other crap

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
#
# EXPOSE 5036
#
## envvars...
#
# ENTRYPOINT ["/some crap"]
