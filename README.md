# Branch conventions

## NEVER push directly to `main`!

`default branch`: main

**새 feature 작업 시 브랜치 명칭: feat/${작업 중인 feature}** (e.g.: feat/login)

**commit 시 명칭: ${type}: ${changes}** (e.g.: feat: add login UI, style: remove empty line)
- commit 내역 확인 예정으로, author가 본인 이메일이 맞는지 각자 검토할 것
- pair끼리 균등한 commit 지향

Merge: pair끼리 1차 검증 후 PR 보내면 각 iteration의 PM이 리뷰 후 merge. 강의 권장사항을 따라 **create new merge commit**(rebase, squash 지양).
- 머지 후 merged branch는 삭제
- 반드시 integration test 후 merge


~~You can use the README file to showcase and promote your mobile app. The template provided below is just a starting point. Feel free to craft your README as you see fit.~~
~~Please note that the README doesn't affect your grade and is not included in documentation(Wiki).~~

# Our app : Eat and Tell (EaT)

Our app is a social network dedicated to food reviews, designed to enrich users' dining experiences by enabling them to share their reviews, connect with like-minded friends, and receive personalized user tags derived from review analysis.

![Application Screenshot](path_to_screenshot.png)

## Features

- Feature 1: Brief description
- Feature 2: Brief description
- ...

## Getting Started

### Prerequisites

- Android Studio [version, e.g., 4.2.1]
- Minimum Android SDK Version [e.g., 21]

### Installation

[Installation link here]
