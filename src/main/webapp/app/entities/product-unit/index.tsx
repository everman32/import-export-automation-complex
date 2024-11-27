import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import ProductUnit from './product-unit';
import ProductUnitDetail from './product-unit-detail';
import ProductUnitUpdate from './product-unit-update';
import ProductUnitDeleteDialog from './product-unit-delete-dialog';

const ProductUnitRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<ProductUnit />} />
    <Route path="new" element={<ProductUnitUpdate />} />
    <Route path=":id">
      <Route index element={<ProductUnitDetail />} />
      <Route path="edit" element={<ProductUnitUpdate />} />
      <Route path="delete" element={<ProductUnitDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default ProductUnitRoutes;
