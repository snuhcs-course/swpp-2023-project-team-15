# Branch conventions

## NEVER push directly to `main`! (except for README changes)

`default branch`: main

**새 feature 작업 시 브랜치 명칭: feat/${작업 중인 feature}** (e.g.: feat/login)

**commit 시 명칭: ${type}: ${changes}** (e.g.: feat: add login UI, style: remove empty line)
- commit 내역 확인 예정으로, author가 본인 이메일이 맞는지 각자 검토할 것
- pair끼리 균등한 commit 지향

Merge: pair끼리 1차 검증 후 PR 보내면 각 iteration의 PM이 리뷰 후 merge. 강의 권장사항을 따라 **create new merge commit**(rebase, squash 지양).
- 머지 후 merged branch는 삭제
- 반드시 integration test 후 merge


# Our app : Eat and Tell (EaT)

<img src="https://github.com/snuhcs-course/swpp-2023-project-team-15/assets/106653382/d84fddd3-65a6-4ddb-99ab-f0d6f9452691" width="150" height="150"/>


EatAndTell is a restaurant review-dedicated SNS, motivated by the lack of personalization in existing restaurant review services. Just simply write restaurant reviews! Based on your reviews, our app creates tags for you to capture your taste. 
With EatAndTell, enjoy a best-fit SNS tailored to your personal tastes and enhance your dining quality!



## Features

Refer to Wiki Documents


## Getting Started

### Prerequisites

- Android Studio Giraffe | 2022.3.1 Patch 1
- Minimum Android SDK Version 24

