#baseline
FROM node:21-alpine AS base
RUN apk add curl
RUN mkdir -p /home/node/app
RUN chown -R node:node /home/node && chmod -R 770 /home/node
WORKDIR /home/node/app

#server-side production dependencies: requires build tools (make, python, g++, etc)
FROM base AS builder-server
WORKDIR /home/node/app
RUN apk add --no-cache --virtual .build-deps git make python3 g++
COPY --chown=node:node ./package.json ./package.json
COPY --chown=node:node ./package-lock.json ./package-lock.json
USER node
RUN npm install --loglevel warn --production

# builds production client-side, and is used by development pantry-web
FROM base AS builder-client
WORKDIR /home/node/app
COPY --chown=node:node . ./
USER node
RUN npm install --loglevel warn
RUN npm run build
EXPOSE 3000
CMD ["npm", "start"]

# production runtime; excludes build tools like: g++ and make
FROM base AS production
WORKDIR /home/node/app
USER node
COPY --chown=node:node --from=builder-client /home/node/app/build ./build/
COPY --chown=node:node --from=builder-server /home/node/app/node_modules ./node_modules
COPY --chown=node:node ./package.json ./package.json
COPY --chown=node:node ./package-lock.json ./package-lock.json
COPY --chown=node:node ./public ./public
EXPOSE 3000
CMD ["npm", "run", "server"]
