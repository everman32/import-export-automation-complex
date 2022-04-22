import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import ExportProd from './export-prod';
import ExportProdDetail from './export-prod-detail';
import ExportProdUpdate from './export-prod-update';
import ExportProdDeleteDialog from './export-prod-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={ExportProdUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={ExportProdUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={ExportProdDetail} />
      <ErrorBoundaryRoute path={match.url} component={ExportProd} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={ExportProdDeleteDialog} />
  </>
);

export default Routes;
