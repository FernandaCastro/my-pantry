#baseline
FROM node:21-alpine AS base
ARG API_URL_PANTRY
ARG API_URL_PURCHASE
ARG API_URL_ACCOUNT
ARG GOOGLE_CLIENT_ID
ENV REACT_APP_API_URL_PANTRY ${API_URL_PANTRY}
ENV REACT_APP_API_URL_PURCHASE ${API_URL_PURCHASE}
ENV REACT_APP_API_URL_ACCOUNT ${API_URL_ACCOUNT}
ENV REACT_APP_GOOGLE_CLIENT_ID ${GOOGLE_CLIENT_ID}
RUN echo REACT_APP_API_URL_PANTRY = $REACT_APP_API_URL_PANTRY
RUN echo REACT_APP_API_URL_PURCHASE = $REACT_APP_API_URL_PURCHASE
RUN echo REACT_APP_API_URL_ACCOUNT = $REACT_APP_API_URL_ACCOUNT
RUN echo REACT_APP_GOOGLE_CLIENT_ID = $REACT_APP_GOOGLE_CLIENT_ID

RUN apk add curl
RUN mkdir -p /home/node/app
RUN chown -R node:node /home/node && chmod -R 770 /home/node

WORKDIR /home/node/app
USER node

COPY --chown=node:node ./package.json ./package.json
COPY --chown=node:node ./package-lock.json ./package-lock.json
COPY --chown=node:node ./src ./src
COPY --chown=node:node ./public ./public

RUN npm install --loglevel warn
RUN npm run build

EXPOSE 3000

CMD ["npm", "run", "server"]
