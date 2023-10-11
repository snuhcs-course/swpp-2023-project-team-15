# Install Dependencies

First, the project requires python version above 3.11. If not, install pyenv:

`curl https://pyenv.run | bash`

After installation, check the log message and follow the instruction to add pyenv to your shell. (Modify `~/.bashrc`)

Then, install python 3.11.0:

`pyenv install 3.11.0`

Then set the global python version:

`pyenv global 3.11.0`

After python version above 3.11 is installed, install poetry:

`curl -sSL https://install.python-poetry.org | python3 -`

Then navigate to backend root and run:

`poetry install`

# On adding new dependencies

- Use `poetry add <package-name>` instead of `pip install <package-name>`
- After package is added by others, run `poetry install` to install the new package

# Environment Variable
Get your environment variable from https://www.notion.so/Environment-Variable-22dcf6d95d294957a6e9b518005cc4ca?pvs=4

```
DB_HOST=xxxx
DB_USER=xxxx
DB_PASSWORD=xxxx
```

# Migrate
`poetry run python manage.py migrate`

# Run
`poetry run python manage.py runserver`

# Admin Account

- Check admin account from https://www.notion.so/Environment-Variable-22dcf6d95d294957a6e9b518005cc4ca?pvs=4
- Login as admin in `/admin`

# Deployment

Normal deployment can be done as following:
- `ssh` into EC2. pem key is at https://www.notion.so/Environment-Variable-22dcf6d95d294957a6e9b518005cc4ca?pvs=4
- `cd` into `~/swpp-2023-project-team-15`
- `git pull`
- `git checkout <branch-name>` where branch name is the branch to be deployed
- In case of new dependency added, `poetry install`
- `sudo systemctl restart gunicorn`

Guricorn service setting is at `/etc/systemd/system/gunicorn.service`. In case this is changed:
- `sudo systemctl daemon-reload`
- `sudo systemctl restart gunicorn`

## Nginx

Nginx configuration file is at `/etc/nginx/conf.d/app.conf`. This is currently configured as following:

```conf
server {
    listen 80;
    server_name ec2-13-125-91-166.ap-northeast-2.compute.amazonaws.com;

    location /static/ {
        alias /home/ec2-user/swpp-2023-project-team-15/backend/staticfiles/;
        expires 30d;
        add_header Cache-Control "public, max-age=2592000";
    }

    location / {
        proxy_pass http://127.0.0.1:8000;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }
}

```

In case this is changed, please run: `sudo systemctl restart nginx`

## Gunicorn

Gunicorn service setting is at `/etc/systemd/system/gunicorn.service`. This is currently configured as following:

```conf
[Unit]
Description=gunicorn daemon for the SWPP 2023 project
After=network.target

[Service]
User=ec2-user
Group=nginx
WorkingDirectory=/home/ec2-user/swpp-2023-project-team-15/backend
ExecStart=/home/ec2-user/.local/bin/poetry run gunicorn config.wsgi:application --bind 127.0.0.1:8000
Restart=always

[Install]
WantedBy=multi-user.target
```

In case this is changed, please run: `sudo systemctl daemon-reload && sudo systemctl restart gunicorn`


## Static files
To serve static files required by e.g. admin sites, one has to first run `poetry run python manage.py collectstatic`.
Then, the static files will be collected to `backend/staticfiles` as specified in `config/settings.py`.

In case of `403 Forbidden` from nginx, one has to run something like below to ensure nginx can access the static files:
```bash
sudo chmod 711 /home/ec2-user/
sudo chown -R ec2-user:nginx /home/ec2-user/swpp-2023-project-team-15/backend/staticfiles/
sudo chmod 755 /home/ec2-user/swpp-2023-project-team-15/backend/
sudo chmod -R 755 /home/ec2-user/swpp-2023-project-team-15/backend/staticfiles/

sudo systemctl restart nginx
```