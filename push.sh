message=$1

git add -A
git commit -m "$message"
git push

echo "pushed to git"
echo $message
