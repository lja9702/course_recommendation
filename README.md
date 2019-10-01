# real-time store recommendation and recognized signboard Android App

이 프로젝트는 유저에게 가게에 대한 정보를 제공하고 유저의 취향에 맞춰 가게와 데이트 코스를 추천하는 안드로이드 프로젝트입니다. 우리는 글자 인식 기술을 이용해 간판을 인식하고 해당 가게에 대한 정보를 띄워줍니다. 또 다른 기능은 코스추천입니다. 사용자와 취향이 비슷한 유저의 가게 정보를 이용하여, 해당 사용자에게 가게를 추천해사주고 이 가게들을 포함한 코스를 만들어 줍니다.

this project is an Android project that provides users with information about the store and recommends stores and dating courses to their tastes. We use text recognition technology to recognize signboard and show information about the store. Another function is course recommendations. Using the stores infomations tof users with similar tastes, we recommend the store to the user and create a course that includes these stores. 



---

# requirement and setting
이 프로젝트에서는 firebase의 DB, ML Kits, function을 이용하였습니다. ML Kits의 경우 text recognization을 사용하였고, function의 경우 Real-time store recommendation을 위해 사용하였습니다. 

In this project, we used firebase's DB, ML Kits, and functions. we used text recognization in ML Kits and functions for real-time store recommendation system.

To use the Android project, the following settings are required.

* NDK, SDK
* **local.properties**
  * ndk.dir, sdk.dir -> 본인 프로젝트의 ndk와 sdk의 directory 주소로 변경. 
* **CMakeList.txt**
  * set(pathPROJECT /home/lja9702/StudioProjects/course_recommendation)... -> 본인 프로젝트 local directory 주소로 변경.
  

---


# Real-time store (course) recommendation

This project recommended the store using Real-time **user-based collaborative filtering**. We used the firebase function to run on the ackend server instead of directly uploading it to Android. See the dataInfo branch for this. 

ref) 실시간 추천엔진 머신한대에 구겨넣기 
https://www.slideshare.net/deview/261-52784785
https://yeoubi.net/2019/04/07/2019-04-07-50-lines-of-recommendation-engine/


---


# API
## Data Set (for store information)
서울시 공공 데이터
1. 서울시 지정/인증업소 현황
https://data.seoul.go.kr/dataList/datasetView.do?infId=OA-2741&srvType=S&serviceKind=1&currentPageNo=1
2. 서울특별시 문화행사 정보
http://data.seoul.go.kr/dataList/datasetView.do?infId=OA-15486&srvType=S&serviceKind=1&currentPageNo=1

## Tmap API
사용자가 직접 선택하거나 추천을 받은 가게들의 좌표를 넘겨 자동으로 코스를 만들어 준다.

## Naver API

서울시 공공데이터의 데이터셋을 보강하기 위해 이용.
