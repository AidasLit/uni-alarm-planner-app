App to create modular alarm notifications and add them to the calendar

# Git reminder

to check status of changes
>git status

to add all changed files to staging
>git add -A

to commit changes
>git commit -m "message"

to push commits to remote
>git push

to download latest branch and start editing it (different from git fetch, look up the difference)
>git pull

to create a new branch in remote from a current new branch
>git push --set-upstream origin blank

remove all changes that arent pushed
>git reset --hard origin

# Common workflow

I've done some work, I'd like to save it.
>git status
>git add -A
>git commit -m "commit message"
>git push

I want to download the latest changes.
>git checkout master
>git pull

I don't have my own branch, I want to branch off and have a seperate environment to develop.
>git checkout master
>git pull
>git branch blank
>git checkout blank
>git push --set-upstream origin blank
