import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import Grade from './grade';
import GradeDetail from './grade-detail';
import GradeUpdate from './grade-update';
import GradeDeleteDialog from './grade-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={GradeUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={GradeUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={GradeDetail} />
      <ErrorBoundaryRoute path={match.url} component={Grade} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={GradeDeleteDialog} />
  </>
);

export default Routes;
