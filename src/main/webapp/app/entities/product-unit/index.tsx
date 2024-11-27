import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import ProductUnit from './product-unit';
import ProductUnitDetail from './product-unit-detail';
import ProductUnitUpdate from './product-unit-update';
import ProductUnitDeleteDialog from './product-unit-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={ProductUnitUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={ProductUnitUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={ProductUnitDetail} />
      <ErrorBoundaryRoute path={match.url} component={ProductUnit} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={ProductUnitDeleteDialog} />
  </>
);

export default Routes;
