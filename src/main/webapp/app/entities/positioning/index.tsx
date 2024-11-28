import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Positioning from './positioning';
import PositioningDetail from './positioning-detail';
import PositioningUpdate from './positioning-update';
import PositioningDeleteDialog from './positioning-delete-dialog';

const PositioningRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Positioning />} />
    <Route path="new" element={<PositioningUpdate />} />
    <Route path=":id">
      <Route index element={<PositioningDetail />} />
      <Route path="edit" element={<PositioningUpdate />} />
      <Route path="delete" element={<PositioningDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default PositioningRoutes;
