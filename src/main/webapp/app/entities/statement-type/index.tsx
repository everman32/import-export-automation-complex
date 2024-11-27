import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import StatementType from './statement-type';
import StatementTypeDetail from './statement-type-detail';
import StatementTypeUpdate from './statement-type-update';
import StatementTypeDeleteDialog from './statement-type-delete-dialog';

const StatementTypeRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<StatementType />} />
    <Route path="new" element={<StatementTypeUpdate />} />
    <Route path=":id">
      <Route index element={<StatementTypeDetail />} />
      <Route path="edit" element={<StatementTypeUpdate />} />
      <Route path="delete" element={<StatementTypeDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default StatementTypeRoutes;
