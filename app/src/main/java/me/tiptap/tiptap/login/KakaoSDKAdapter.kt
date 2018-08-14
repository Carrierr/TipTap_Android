package me.tiptap.tiptap.login

import com.kakao.auth.*
import me.tiptap.tiptap.TipTapApplication

open class KakaoSDKAdapter : KakaoAdapter() {

    /**
     * Session Config에 대해서는 default 값들이 존재한다.
     * 필요한 상황에서만 override해서 사용하면 됨.
     * @return Session의 설정값.
     */
    override fun getSessionConfig(): ISessionConfig = object : ISessionConfig {

        override fun getAuthTypes(): Array<AuthType> = arrayOf(AuthType.KAKAO_TALK)

        override fun isSecureMode(): Boolean = true

        override fun isUsingWebviewTimer(): Boolean = false

        override fun getApprovalType(): ApprovalType = ApprovalType.INDIVIDUAL

        override fun isSaveFormData(): Boolean = true
    }


    override fun getApplicationConfig(): IApplicationConfig =
            IApplicationConfig { TipTapApplication.getTipTapApplicationContext() }
}