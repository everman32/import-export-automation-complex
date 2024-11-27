import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Transport from './transport';
import TransportDetail from './transport-detail';
import TransportUpdate from './transport-update';
import TransportDeleteDialog from './transport-delete-dialog';

const TransportRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Transport />} />
    <Route path="new" element={<TransportUpdate />} />
    <Route path=":id">
      <Route index element={<TransportDetail />} />
      <Route path="edit" element={<TransportUpdate />} />
      <Route path="delete" element={<TransportDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default TransportRoutes;
