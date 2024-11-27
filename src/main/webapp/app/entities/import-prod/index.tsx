import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import ImportProd from './import-prod';
import ImportProdDetail from './import-prod-detail';
import ImportProdUpdate from './import-prod-update';
import ImportProdDeleteDialog from './import-prod-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={ImportProdUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={ImportProdUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={ImportProdDetail} />
      <ErrorBoundaryRoute path={match.url} component={ImportProd} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={ImportProdDeleteDialog} />
  </>
);

export default Routes;
