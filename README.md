# 심플뮤직플레이어 프로젝트
## 1. 개발환경
1. 개발 툴 
    * Android Studio 2.3
2. SDK 버전
    * minSdkVersion 22
    * targetSdkVersion 22
    * compileSdkVersion 25
3. 버전관리 
    * Git

## 2. 개발 기간
    2017년 3월 23일 ~ 2017년 5월 23일
    
## 3. UI

1. ViewPager, TabLayout으로 3개의 화면(플레이어, 재생목록, 즐겨찾는 재생목록)으로 구성하였습니다. 
반면, 하단의 '뮤직컨트롤러'는 세 화면 중 어디에서나 보이게끔 고정하였습니다.
 
1. 우선, 앱을 실행하면 가장 먼저 뜨는 재생목록 화면에서 '곡추가' 버튼을 눌러 기기에 저장된 음악파일을 불러올 수 있게 하였습니다.

    ![screensh](https://github.com/HyunjuLee521/MusicPlayerProject/blob/master/ui1.png)

1. 곡명 혹은 아티스트명으로 구분하여 기기에 저장된 음악 파일을 보여줍니다. 원하는 음악들을 선택하고 '플레이리스트에 추가하기' 버튼을 눌러 재생목록에 추가합니다.

1. 재생목록에서 음악을 선택하여 재생합니다. '플레이어'와 하단의 '뮤직컨트롤러'에도 해당 음악 정보가 업데이트 됩니다.

1. 플레이어 화면에서 하트 버튼을 클릭하여, 현재 재생중인 곡을 즐겨찾기에 추가할 수 있습니다.
 
1. 즐겨찾기에 추가된 곡들을 모아 기존의 '재생목록'과는 별개로 '즐겨찾는 재생목록'을 제공합니다.

1. 재생목록 편집기능을 제공합니다.

1. 롤리팝 이상에서는 알림바가 표시됩니다. 이전/다음 곡 재생, 재생/일시정지, 앱 종료 기능이 있습니다. 

1. 잠금화면에서도 알림이 표시됩니다. 


## 4. Sutructure(Xmind)
![screensh](https://github.com/HyunjuLee521/MusicPlayerProject/blob/master/structure.png)

## 5. Features
1. 플레이어, 재생목록, 즐겨찾기 재생목록 제공
2. 기기에 저장되어 있는 곡 가수 별로 불러오기
3. 알림바/잠금화면에서 이전/다음 곡 넘김, 재생/일시정지, 앱 종료
    * 잠금화면에서의 조작은 Android Lollipop 부터 가능합니다.
4. 1회 재생, 전체 반복, 셔플
5. 플레이어에서 현재 재생중인 곡 즐겨찾기에 추가
6. 이전에 재생하던 곡 목록을 실행시 복원
7. 다른 미디어 플레이어가 재생될 때, 해당 어플에서 재생 중이던 곡 자동 일시 정지

## 6. Credits

1. 안드로이드 이벤트 버스
    * greenrobot / EventBus : https://github.com/greenrobot/EventBus
2. 렘
    * realm : https://github.com/realm
    


## 7. License
Copyright 2017. Hyunju Lee

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.




