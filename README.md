# sp-barcode-sample

 바코드 이미지를 만들어내는 Spring boot 애플리케이션 샘플 입니다.
 
 ```
 서버를 실행하고 
     http://localhost:8800/bar?code=123456789 
 를 호출하면 아래와 같은 결과를 얻을 수 있다.
 ```
 
 ![Barcode Image](./img/sample.png?raw=true "Sample Barcode Image")
 
 
 그 밖에 추가로 사용 가능한 argument
 * width : 바코드 이미지의 너비
 * height : 바코드 이미지의 높이
 * fontSize : 바코드 문자 크기 (문자 크기를 0으로 설정하면 코드가 보이지 않는다.)