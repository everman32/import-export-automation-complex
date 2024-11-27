import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import Transport from './transport';
import TransportDetail from './transport-detail';
import TransportUpdate from './transport-update';
import TransportDeleteDialog from './transport-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={TransportUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={TransportUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={TransportDetail} />
      <ErrorBoundaryRoute path={match.url} component={Transport} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={TransportDeleteDialog} />
  </>
);

export default Routes;
