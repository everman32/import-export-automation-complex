import React from 'react';
import { Switch } from 'react-router-dom';

// eslint-disable-next-line @typescript-eslint/no-unused-vars
import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import Transport from './transport';
import Driver from './driver';
import Address from './address';
import Product from './product';
import Trip from './trip';
import ImportProd from './import-prod';
import ExportProd from './export-prod';
import Grade from './grade';
import Statement from './statement';
import StatementType from './statement-type';
import Positioning from './positioning';
import ProductUnit from './product-unit';
/* jhipster-needle-add-route-import - JHipster will add routes here */

const Routes = ({ match }) => (
  <div>
    <Switch>
      {/* prettier-ignore */}
      <ErrorBoundaryRoute path={`${match.url}transport`} component={Transport} />
      <ErrorBoundaryRoute path={`${match.url}driver`} component={Driver} />
      <ErrorBoundaryRoute path={`${match.url}address`} component={Address} />
      <ErrorBoundaryRoute path={`${match.url}product`} component={Product} />
      <ErrorBoundaryRoute path={`${match.url}trip`} component={Trip} />
      <ErrorBoundaryRoute path={`${match.url}import-prod`} component={ImportProd} />
      <ErrorBoundaryRoute path={`${match.url}export-prod`} component={ExportProd} />
      <ErrorBoundaryRoute path={`${match.url}grade`} component={Grade} />
      <ErrorBoundaryRoute path={`${match.url}statement`} component={Statement} />
      <ErrorBoundaryRoute path={`${match.url}statement-type`} component={StatementType} />
      <ErrorBoundaryRoute path={`${match.url}positioning`} component={Positioning} />
      <ErrorBoundaryRoute path={`${match.url}product-unit`} component={ProductUnit} />
      {/* jhipster-needle-add-route-path - JHipster will add routes here */}
    </Switch>
  </div>
);

export default Routes;
