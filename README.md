# Fitness App

For working on your code, make sure you're in the directory of the source code on your terminal or GitHub Desktop.

## Initialization and Connection

From what I know, all of us have initialized Git and connected our local git directories to the remote GitHub repository. To recap, we ran the command:

```git init```

in our local repository which had the source code. Then, we connected it to the GitHub repository using:

```git remote add origin https://github.com/chidoluezikora/fitnessApp.git```

## Pulling Changes and Updating Branches

Every time you would like to make changes to the source code, first pull from the main branch on the remote to the main branch on your local branch. Check the branch you are on by running the command:

```git branch```

It should highlight the branch you're currently on. If you're not on the main branch, navigate to the main branch by running the command:

```git checkout main```

Now you're on main. The next thing you need to do is to pull all changes from the remote's main branch to your local main branch:

```git fetch```

```git pull origin main```

Great! Now navigate to your own local branch:

```git checkout yourname```

You would also need to pull from the remote's main branch to your own local branch to update your branch with all the changes we've approved:

```git pull origin main```

## Editing the Code

Before you edit the code in Android studio, add new files, folders and so on, make sure you are still on your own local branch, which you should:

```git branch```

However, if you're not for some reason, navigate to it:

```git checkout yourname```

Now edit and make changes to the source code as you see fit. You can always add the changes to the staging area by:

```git add .```

Commit those changes to solidify those changes by:

```git commit -m "what i did in present tense(your commit message)"```

## Pushing Changes to Remote Repository

However, before you push to your branch in the remote repo, note that you should never push to the remote's main branch from your local directory! The last two steps must be done, if not you won't push anything. Now push to your remote branch by:

```git push origin yourname```

Great! The next steps need to be implemented in the GitHub website. Open the fitnessApp repo on GitHub and navigate to your branch there and click on "Compare and Pull Request" and confirm the other green button below. But please do not click on the purple "Merge" button that comes up until the changes are approved!

Happy Coding!
