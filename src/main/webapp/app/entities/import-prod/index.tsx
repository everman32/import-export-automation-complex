import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import ImportProd from './import-prod';
import ImportProdDetail from './import-prod-detail';
import ImportProdUpdate from './import-prod-update';
import ImportProdDeleteDialog from './import-prod-delete-dialog';

const ImportProdRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<ImportProd />} />
    <Route path="new" element={<ImportProdUpdate />} />
    <Route path=":id">
      <Route index element={<ImportProdDetail />} />
      <Route path="edit" element={<ImportProdUpdate />} />
      <Route path="delete" element={<ImportProdDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default ImportProdRoutes;
