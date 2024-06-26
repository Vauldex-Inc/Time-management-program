import { setupWorkspaceEnvironment } from '~/utils/init'

export default defineNuxtPlugin(async () => {
  if (import.meta.server) {
    // #region Initialize environment
    await setupWorkspaceEnvironment()
    // #endregion
  }
})
