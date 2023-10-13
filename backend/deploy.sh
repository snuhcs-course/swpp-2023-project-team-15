#!/bin/bash
git pull
poetry install
sudo systemctl restart gunicorn