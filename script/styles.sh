#!/usr/bin/env bash
./node_modules/node-sass/bin/node-sass ./src/styles/app.scss ./resources/public/app.css --include-path ./node_modules --output-style compressed $@