import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import ExportProd from './export-prod';
import ExportProdDetail from './export-prod-detail';
import ExportProdUpdate from './export-prod-update';
import ExportProdDeleteDialog from './export-prod-delete-dialog';

const ExportProdRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<ExportProd />} />
    <Route path="new" element={<ExportProdUpdate />} />
    <Route path=":id">
      <Route index element={<ExportProdDetail />} />
      <Route path="edit" element={<ExportProdUpdate />} />
      <Route path="delete" element={<ExportProdDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default ExportProdRoutes;
