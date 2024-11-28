import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Statement from './statement';
import StatementDetail from './statement-detail';
import StatementUpdate from './statement-update';
import StatementDeleteDialog from './statement-delete-dialog';

const StatementRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Statement />} />
    <Route path="new" element={<StatementUpdate />} />
    <Route path=":id">
      <Route index element={<StatementDetail />} />
      <Route path="edit" element={<StatementUpdate />} />
      <Route path="delete" element={<StatementDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default StatementRoutes;
