<template>
  <Header />
    <div class="login-container">
      <h1>회원 가입</h1>
      <form @submit.prevent="submitForm">
        <div>
          <div class="input-wrapper">
            <input
              type="email"
              v-model="member.memEmail"
              required
              placeholder="이메일을 입력하세요"
              :class="{'input-error-border': !valid.email}"
            />
            <span v-if="isVerified" class="check-icon">✓</span>
          </div>
          <p v-show="!valid.email" class="input-error">
            이메일 주소를 정확히 입력해주세요.
          </p>
        </div>
        <div v-if="valid.email && !isVerified">
          <button type="button" 
                  @click="sendVerificationCode" 
                  :disabled="!valid.email || verificationSent || isEmailSendLoading" 
                  v-if="!verificationSent">
              {{ isLoading ? '이메일 전송 중...' : '인증번호 발송' }}
          </button>
          <div v-if="verificationSent" >
            <input
              type="text"
              v-model="verificationCode"
              placeholder="인증번호 6자리 입력"
              maxlength="6"
              :class="{'input-error-border': !valid.verificationCode}"
              />
            <p v-show="!valid.verificationCode" class="input-error">
                인증번호가 일치하지 않습니다.
            </p>
            <div class="verification-info">
              <span>남은 시간: {{ remainingTime }}</span>
              <a href="#" 
                class="resend-link" 
                @click.prevent="sendVerificationCode">
                이메일이 오지 않을 경우 재전송
              </a>
            </div>
            <button type="button" @click="verifyCode">확인</button>
          </div>
        </div>
        <div>
          <label for="password">비밀번호:</label>
          <input
            type="password"
            v-model="member.memPassword"
            required
            placeholder="비밀번호를 입력하세요"
            :class="{'input-error-border': !valid.password}"
          />
          <p v-show="!valid.password" class="input-error">
            비밀번호는 대소문자, 숫자, 특수기호(@$!%*?&#)를 포함한 8자 이상입니다.          </p>
        </div>
        <div>
          <label for="passwordConfirm">비밀번호 확인:</label>
          <input
            type="password"
            v-model="member.memPasswordConfirm"
            required
            placeholder="비밀번호를 다시 입력하세요"
            :class="{'input-error-border': !valid.passwordConfirm}"
          />
          <p v-show="!valid.passwordConfirm" class="input-error">
            비밀번호가 일치하지 않습니다.
          </p>
        </div>
        <div>
          <label for="name">이름:</label>
          <input
            type="text"
            v-model="member.memName"
            required
            placeholder="이름을 입력하세요"
          />
        </div>
        <div>
          <label for="phone">전화번호:</label>
          <input
            type="text"
            v-model="member.memPhone"
            required
            placeholder="전화번호를 입력하세요"
            :class="{'input-error-border': !valid.phone}" 
            @input="formatPhoneNumber" 
            maxlength="13" 
          />
          <p v-show="!valid.phone" class="input-error">
            전화번호 형식이 올바르지 않습니다. (ex: 010-1234-5678)
          </p>
        </div>
        <button type="submit">가입하기</button>
      </form>
    </div>
    <Footer />

</template>
  
<script>
  import Header from "../../MainPage/components/Header.vue";
  import Footer from "../../MainPage/components/Footer.vue";
  
  export default {
    name: "SignUp",
    data() {
      return {
        member: {
          memEmail: "",
          memPassword: "",
          memPasswordConfirm: "",  // 비밀번호 확인용 추가
          memName: "",
          memPhone: "",
        },
        valid: {
          email: true,  //이메일 유효성 검사 결과
          phone: true,  //전화번호 유효성 검사 결과
          password: true, // 비밀번호 유효성 검사 결과
          passwordConfirm: true,  // 비밀번호 확인 검증용 추가
          verificationCode: true  // 인증번호 유효성 상태 추가
        },
        verificationSent: false,
        verificationCode: "",
        timer: null,
        remainingTime: "5:00",
        isVerified: false,
        verifiedEmail: "",
        isEmailSendLoading: false,
      };
    },
    components: {
    Header,  
    Footer
  },
    watch:{
      "member.memEmail"(newEmail){
        this.checkEmail(newEmail);
      },
      "member.memPhone"(newPhone){
        this.checkPhone(newPhone);
      },
      "member.memPassword"(newPassword){
        this.checkPassword(newPassword);
      },
      "member.memPasswordConfirm"(newPasswordConfirm) {
        this.checkPasswordConfirm(newPasswordConfirm);
      },
    },
    methods: {
      checkEmail(email){
        const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
        this.valid.email = emailRegex.test(email);
      },
      checkPhone(phone){
        const phoneRegex = /^01[0-9]-\d{3,4}-\d{4}$/;
        this.valid.phone = phoneRegex.test(phone);
      },
      checkPassword(password) {
        const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&#])[A-Za-z\d@$!%*?&#]{8,}$/;
        this.valid.password = passwordRegex.test(password);
      },
      checkPasswordConfirm(passwordConfirm) {
        this.valid.passwordConfirm = passwordConfirm === this.member.memPassword;
      },
      async submitForm() {
        if(!this.valid.email || !this.valid.phone || !this.valid.passwordConfirm){
          alert("입력값을 확인하세요.");
          return;
        }
        if (!this.isVerified) { // 이메일 인증번호 확인
          alert("이메일 인증을 완료해주세요.");
          return;
        }
        if (this.member.memEmail !== this.verifiedEmail) {
          alert("인증받은 이메일로만 가입이 가능합니다.");
          return;
        }
        try {
          // 서버로 POST 요청, member 객체에 맞춰 데이터 전송
          const response = await this.$axios.post("/members/user", {
            memEmail: this.member.memEmail,
            memPassword: this.member.memPassword,
            memName: this.member.memName,
            memPhone: this.member.memPhone,
            verifiedCode: this.verificationCode,
          });
  
          console.log("회원 가입 성공:", response.data);
          alert("회원 가입에 성공했습니다!");
  
          // 성공 시 리다이렉트
          this.$router.push("/");
        } catch (error) {
          console.error("회원 가입 실패:", error.response?.data || error.message);
          alert("회원 가입에 실패했습니다!");
        }
      },
      async sendVerificationCode() {
        if (!this.valid.email) {
          alert("유효한 이메일을 입력해주세요.");
          return;
        }

        // 기존 타이머가 있다면 정리
        if (this.timer) {
          clearInterval(this.timer);
          this.timer = null;
        }

        this.isEmailSendLoading = true; // 로딩 시작(버튼 비활성화)

        try {
          // 서버에 인증번호 발송 요청
          const response = await this.$axios.post("/emails/verifications", {
            email: this.member.memEmail
          });
          
          this.verificationSent = true;
          this.startTimer();
          // alert("인증번호가 발송되었습니다.");
        } catch (error) {
          alert("인증번호 발송에 실패했습니다.");
        } finally {
          this.isEmailSendLoading = false; // 로딩 종료
        }
      },
      // 인증번호 유효시간 타이머
      startTimer() {
        let verifiedTime = 300;
        let time = verifiedTime; // 5분
        this.timer = setInterval(() => {
          time--;
          const minutes = Math.floor(time / 60);
          const seconds = time % 60;
          this.remainingTime = `${minutes}:${seconds.toString().padStart(2, '0')}`;

          if (time <= 0) {
            clearInterval(this.timer);
            this.verificationSent = false;
            this.serverCode = "";
            alert("인증 시간이 만료되었습니다.");
            time = verifiedTime;
          }
        }, 1000);
      },
      // 인증 번호 확인
      async verifyCode() {
        try {
            const response = await this.$axios.put("/emails/verifications", {
                email: this.member.memEmail,
                code: this.verificationCode
            });

            // HTTP 상태 코드가 200이면 성공
            if (response.status === 200) {
                this.isVerified = true;
                this.verifiedEmail = this.member.memEmail;
                this.valid.verificationCode = true;  // 성공 시 에러 표시 제거
                clearInterval(this.timer);
                alert("인증이 완료되었습니다.");
            }
        } catch (error) {
            console.error("인증 확인 실패:", error);
            // alert(error.response?.data?.message || "인증번호가 일치하지 않습니다.");
            this.valid.verificationCode = false;  // 실패 시 에러 표시
            this.isVerified = false;
        }
      },
      // 전화번호 자동 - 추가
      formatPhoneNumber() {
        let num = this.member.memPhone.replace(/\D/g, ""); // 숫자만 남김
        if (num.length > 3 && num.length <= 7) {
          this.member.memPhone = `${num.slice(0, 3)}-${num.slice(3)}`;
        } else if (num.length > 7) {
          this.member.memPhone = `${num.slice(0, 3)}-${num.slice(3, 7)}-${num.slice(7, 11)}`;
        } else {
          this.member.memPhone = num;
        }
      },
      // 컴포넌트가 제거될 때 타이머 정리
      beforeDestroy() {
        if (this.timer) {
          clearInterval(this.timer);
        }
      },
    },
  };
</script>
  
<style>
  .login-container {
    max-width: 400px;
    margin: auto;
    padding: 20px;
    border: 1px solid #ddd;
    border-radius: 8px;
  }
  .login-container h1 {
    text-align: center;
  }
  .login-container div {
    margin-bottom: 15px;
  }
  .login-container label {
    display: block;
    font-weight: bold;
  }
  .login-container input {
    width: 100%;
    padding: 8px;
    box-sizing: border-box;
  }
  button {
    width: 100%;
    padding: 10px;
    background-color: #007bff;
    color: white;
    border: none;
    border-radius: 4px;
    cursor: pointer;
  }
  button:hover {
    background-color: #0056b3;
  }
  .input-error{
    line-height: 16px;
    color: #ff4444;
  }
  .input-error-border {
    border: 2px solid #ff4444;
    animation: shake 0.5s;
  }
  .email-container {
    position: relative;
  }
  .input-success {
    border-color: #28a745;
    padding-right: 30px;
  }
  .check-icon {
    position: absolute;
    right: 10px;
    top: 50%;
    transform: translateY(-50%);
    color: #28a745;
    font-size: 1.2em;
  }
  .verification-info {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin: 10px 0;
  }

  .resend-link {
    color: #007bff;
    text-decoration: none;
    font-size: 0.9em;
    cursor: pointer;
  }

  .resend-link:hover {
    text-decoration: underline;
    color: #0056b3;
  }

  button:disabled {
    background-color: #cccccc;
    cursor: not-allowed;
  }

  .input-wrapper {
    position: relative;
  }

  .input-wrapper input {
    width: 100%;
    padding-right: 30px; /* 체크 아이콘을 위한 여백 */
  }

  .check-icon {
    position: absolute;
    right: 10px;
    top: 50%;
    transform: translateY(-50%);
    color: #28a745;
    font-size: 1.2em;
  }

</style>
  