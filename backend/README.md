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

# Testing

To run test and get coverage, do following:
- `poetry install`
- `poetry shell`
- `python3 -m coverage run manage.py test`
- `python3 -m coverage report`

The result would be something like:
```
❯ python3 -m coverage report
Name                    Stmts   Miss  Cover
-------------------------------------------
<...omitted>
users/__init__.py           0      0   100%
users/admin.py              7      0   100%
users/apps.py               4      0   100%
users/models.py             8      0   100%
users/serializers.py       46     12    74%
users/urls.py               4      0   100%
users/views.py             95     60    37%
-------------------------------------------
TOTAL                     476    176    63%
```

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

# EC2 Deployment

Deployment can be done as following:
- First, check you can `ssh` into EC2. pem key is at https://www.notion.so/Environment-Variable-22dcf6d95d294957a6e9b518005cc4ca?pvs=4
- Push your code to any branch on origin.
- Run `./deploy.sh`. Then, the current branch on local is deployed on server. You might need to change the KEY variable in the script.


## Caddy

For auto-https, we use Caddy instead of Nginx as reverse proxy. For installing Caddy on Amazon Linux 2, see https://stackoverflow.com/a/74436450

Caddy service setting is at `/etc/caddy/Caddyfile`. The file is currently configured as following:
```
http://ec2-13-125-91-166.ap-northeast-2.compute.amazonaws.com, swpp.dlwocks31.me {
    handle_path /static/* {
        root * /home/ec2-user/swpp-2023-project-team-15/backend/staticfiles/
        file_server
    }

    handle_path /* {
        reverse_proxy :8000
    }
}
```

In case this is changed, please run: `sudo systemctl daemon-reload && sudo systemctl restart caddy`

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