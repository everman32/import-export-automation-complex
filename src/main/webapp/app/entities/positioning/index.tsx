import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import Positioning from './positioning';
import PositioningDetail from './positioning-detail';
import PositioningUpdate from './positioning-update';
import PositioningDeleteDialog from './positioning-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={PositioningUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={PositioningUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={PositioningDetail} />
      <ErrorBoundaryRoute path={match.url} component={Positioning} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={PositioningDeleteDialog} />
  </>
);

export default Routes;
