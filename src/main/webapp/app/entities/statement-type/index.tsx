import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import StatementType from './statement-type';
import StatementTypeDetail from './statement-type-detail';
import StatementTypeUpdate from './statement-type-update';
import StatementTypeDeleteDialog from './statement-type-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={StatementTypeUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={StatementTypeUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={StatementTypeDetail} />
      <ErrorBoundaryRoute path={match.url} component={StatementType} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={StatementTypeDeleteDialog} />
  </>
);

export default Routes;
