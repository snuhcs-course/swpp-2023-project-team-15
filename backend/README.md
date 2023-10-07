# Install Dependencies

Install poetry:

`curl -sSL https://install.python-poetry.org | python3 -`

Then navigate to backend root and run:

`poetry install`

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
