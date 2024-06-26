<template>
  <section class="signup-page">
    <aside>
      <LazyCmSignupVerifyModal v-if="$signupVerifyModal.show" />
    </aside>

    <div class="content">
      <div class="hero">
        <CmColorModePicture
          src="/images/main-logo.svg"
          src-dark="/images/main-logo-dark.png"
          alt="logo"
        />
      </div>

      <div class="signup-options">
        <h1>{{ $t('signup.start_tracking') }}</h1>

        <div class="buttons">
          <CmButton
            type="button"
            state="basic"
            @click="handleGoogleSignin"
          >
            <CmImage
              src="/images/google.svg"
              srcset="/images/google.svg"
              alt="google"
            />
            <span class="signup-button-label">{{ $t('signup.google') }}</span>
          </CmButton>

          <CmButton
            type="button"
            state="basic"
            @click="handleAppleSignin"
          >
            <CmIcon
              type="apple"
              size="medium"
              state="primary"
            />
            <span class="signup-button-label">{{ $t('signup.apple') }}</span>
          </CmButton>

          <div class="wrapper">
            <div class="signup-separator">
              <span>{{ $t('common.or') }}</span>
            </div>
          </div>

          <CmButton
            type="button"
            @click="handleCreateAccount"
          >
            {{ $t('signup.create') }}
          </CmButton>

          <div class="consent">
            <p class="consent-title text">
              {{ $t('signup.legality.consent') }}
            </p>

            <p class="consent-link">
              <NuxtLinkLocale
                class="link"
                to="/legal/terms-of-use"
              >
                {{ $t('signup.legality.terms_of_use') }}
              </NuxtLinkLocale>

              <span class="punctuation">{{ $t('signup.legality.comma') }}</span>
            </p>

            <p class="consent-link">
              <NuxtLinkLocale
                class="link"
                to="/legal/privacy-policy"
              >
                {{ $t('signup.legality.privacy_policy') }}
              </NuxtLinkLocale>

              <span class="punctuation">{{ $t('signup.legality.period') }}</span>
            </p>
          </div>
        </div>

        <div class="signin-link">
          <p>{{ $t('signin.ante_link') }}</p>

          <CmButton
            type="button"
            state="secondary"
            role="link"
            @click="handleSigninClick"
          >
            {{ $t('signin.title') }}
          </CmButton>
        </div>
      </div>
    </div>
  </section>
</template>

<script lang="ts" setup>
import { PathNames } from '~/types/static'

definePageMeta({
  layout: 'guest',
  middleware: 'guest'
})

// #region Composables
const { t } = useI18n()
const {
  $signupVerifyModal
} = useNuxtApp()
// #endregion

// #region Stores
const authenticationStore = useAuthenticationStore()
// #endregion

useHead({
  title: t('page.signup')
})

// #region Methods
const handleCreateAccount = () => {
  $signupVerifyModal.show = true
}

const handleSigninClick = () => {
  navigateToLocale(PathNames.Signin)
}

const handleGoogleSignin = () => {
  authenticationStore.authLoginGoogle()
}

const handleAppleSignin = () => {
  authenticationStore.authLoginApple()
}
// #endregion
</script>

<style src="./style.scss" lang="scss" scoped></style>
