# Filmy Privacy Policy

_Last updated: 4 October 2026_

Filmy is an open-source app for discovering movies and TV shows. This policy
explains what data the app handles and who receives it.

Filmy has no servers of its own and no Filmy account. The developers do not
receive, store or sell your personal data. The app does talk to the
third-party services listed below, and it uses Google Firebase to measure
usage and diagnose crashes.

## Data stored on your device

- Your settings: theme, language and region.
- Your recent searches.
- A cache of movie and show details, including titles you marked as watched
  or added to your watchlist.
- If you log in with TMDB: your TMDB profile (name, username, avatar) and the
  credentials that keep you logged in.

This data stays in the app's private storage. Android's backup and
device-transfer features may copy your settings and the cache to your Google
account or a new device. Your TMDB login credentials are excluded from backup
and transfer, so you log in again on a new device.

## Services the app contacts

Every service the app contacts sees your IP address, as with any internet
request.

| Service | Why | What is sent |
| --- | --- | --- |
| [TMDB](https://www.themoviedb.org/privacy-policy) | Movie, show and people data, images, and your TMDB account features | Your searches, the titles you open, your chosen region and language. If you log in: your watchlist, favourites, ratings and lists |
| [OMDb](https://www.omdbapi.com/legal.htm) | Ratings from other sources | The IMDb ID of the title you are viewing |
| [ipwho.is](https://ipwho.is/) | Suggesting your country during first-time setup | A single request, from which the service infers your country |
| YouTube | Trailer thumbnails; trailers open in the YouTube app or your browser | The ID of the trailer |
| Gravatar | Your TMDB profile picture, if it is hosted there | The avatar hash from your TMDB profile |

Logging in with TMDB is optional and happens on TMDB's own website; Filmy
never sees your TMDB password. Logging out asks TMDB to revoke the app's
access and removes your profile and credentials from the device.

## Analytics and crash reports

Filmy uses Google Firebase:

- **Firebase Analytics** collects app usage events (such as screens viewed),
  device and OS information, approximate region derived from your IP address,
  and an app-instance identifier.
- **Firebase Crashlytics** collects crash reports and reports of errors the
  app recovered from, with device model, OS version and an installation
  identifier.

Filmy does not show ads and does not collect your advertising ID. Google
processes this data under the
[Google Privacy Policy](https://policies.google.com/privacy).

## Deleting your data

- Log out in the Account tab to revoke access and remove your TMDB profile
  and credentials from the device.
- Clear the app's storage or uninstall the app to remove everything stored on
  the device.
- Data in your TMDB account is managed on the TMDB website.

## Children

Filmy is not directed at children under 13.

## Changes

Updates to this policy are published in this file; its history is public in
the project's repository.

## Contact

Questions about this policy: webianks@gmail.com, or open an issue at
https://github.com/salRoid/Filmy/issues.
