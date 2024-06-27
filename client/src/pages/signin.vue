<template>
  <div class="content">
    <h3 class="heading-title">
      {{ $t('signin.title') }}
    </h3>

    <CmLanguageSwitcher
      id="language-switcher"
      class="languages"
    />

    <CmAccountForm
      sign-in
      :state="isSubmitting ? 'submitting' : ''"
      @submit="submitSignin"
      @submit-google="loginGoogle"
      @submit-apple="loginApple"
    />
  </div>
</template>

<script lang="ts" setup>
import {
  LocallyStored,
  PathNames,
  SigninError
} from '~/types/static'

definePageMeta({
  layout: 'guest',
  middleware: 'guest'
})

// #region Composables
const { t } = useI18n()
const route = useRoute()
// #endregion

// #region Stores
const authenticationStore = useAuthenticationStore()
const notificationStore = useNotificationStore()
// #endregion

useHead({
  title: t('page.signin')
})

// #region State
const isSubmitting = ref(false)
// #endregion

// #region Methods
const submitSignin = async (form: {
  email: string
  password: string
}) => {
  isSubmitting.value = true

  try {
    await authenticationStore.signin(form)
    navigateToLocale(PathNames.Initializing)
  } finally {
    isSubmitting.value = false
  }
}

const loginGoogle = () => {
  authenticationStore.authLoginGoogle()
}

const loginApple = () => {
  authenticationStore.authLoginApple()
}
// #endregion

// #region Lifecycle
onMounted(() => {
  localStorage.removeItem(LocallyStored.ExistingId)
  if (route.query.flag === SigninError.Disabled) {
    notificationStore.updateNotification({
      message: 'signin.messages.sso_fail',
      isError: true,
      isShow: true
    })
  } else if (route.query.flag === SigninError.Used) {
    notificationStore.updateNotification({
      message: 'signin.messages.link_fail',
      isError: true,
      isShow: true
    })
  }
})
// #endregion
</script>

<style lang="scss" scoped>
.content {
  display: flex;
  flex-direction: column;
  place-self: center;

  @media screen and (min-width: $breakpoint-med) {
    justify-content: unset;
    padding-top: 5rem;
  }

  .heading-title {
    color: var(--text-primary);
    margin-bottom: 20px;
    text-align: center;
  }

  .languages {
    text-align: center;
  }
}
</style>
