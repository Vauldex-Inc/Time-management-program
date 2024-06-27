import { PathNames } from '~/types/static'

export default defineNuxtRouteMiddleware(() => {
  // #region Composables and stores
  const nuxtApp = useNuxtApp()
  const authenticationStore = useAuthenticationStore()
  // #endregion

  if (authenticationStore.isLoggedIn && !nuxtApp.$qr.hasId) {
    return navigateToLocale(PathNames.Timestamps)
  }
})
