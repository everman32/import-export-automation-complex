import { loadingBarReducer as loadingBar } from 'react-redux-loading-bar';

import locale from './locale';
import authentication from './authentication';
import applicationProfile from './application-profile';

import administration from 'app/modules/administration/administration.reducer';
import userManagement from 'app/modules/administration/user-management/user-management.reducer';
import register from 'app/modules/account/register/register.reducer';
import activate from 'app/modules/account/activate/activate.reducer';
import password from 'app/modules/account/password/password.reducer';
import settings from 'app/modules/account/settings/settings.reducer';
import passwordReset from 'app/modules/account/password-reset/password-reset.reducer';
// prettier-ignore
import transport from 'app/entities/transport/transport.reducer';
// prettier-ignore
import driver from 'app/entities/driver/driver.reducer';
// prettier-ignore
import address from 'app/entities/address/address.reducer';
// prettier-ignore
import product from 'app/entities/product/product.reducer';
// prettier-ignore
import trip from 'app/entities/trip/trip.reducer';
// prettier-ignore
import importProd from 'app/entities/import-prod/import-prod.reducer';
// prettier-ignore
import exportProd from 'app/entities/export-prod/export-prod.reducer';
// prettier-ignore
import grade from 'app/entities/grade/grade.reducer';
/* jhipster-needle-add-reducer-import - JHipster will add reducer here */

const rootReducer = {
  authentication,
  locale,
  applicationProfile,
  administration,
  userManagement,
  register,
  activate,
  passwordReset,
  password,
  settings,
  transport,
  driver,
  address,
  product,
  trip,
  importProd,
  exportProd,
  grade,
  /* jhipster-needle-add-reducer-combine - JHipster will add reducer here */
  loadingBar,
};

export default rootReducer;
