# TalentBridge job portal

A responsive, browser-based prototype for the online job portal brief. It uses plain HTML, CSS, and JavaScript, so it has no package install or build step.

## Open it

Open `index.html` in a modern browser. The prototype starts in the job seeker workspace. Use the **Demo as** menu in the top bar to switch among the seeker, employer, and administrator workspaces.

## Included workflows

- Job seekers can search and filter listings, save roles, apply with a note and optional resume selection, update their profile, track application status, and message hiring teams.
- Employers can create and edit listings, submit them for approval, close or delete roles, review applicants, update candidate statuses, exchange messages, and view hiring analytics.
- Administrators can approve or return job listings, pause or restore member accounts, change platform settings, and review activity and marketplace summaries.
- Sample accounts, jobs, applications, and conversations are included. **Reset demo data** restores the starting data.
- Layouts adapt to mobile and desktop screens. Changes are stored in the current browser using `localStorage`.

## Prototype boundary

This is a front-end prototype, not a deployed multi-user service. Its demo role selector is not authentication; browser-local data is not shared between people or devices. Resume selection stores only a file name in the demo and does not upload the file. A production release needs a Java/API backend, real sign-in and role authorization, a database, protected document storage, server-side validation, and deployment configuration.
