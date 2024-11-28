import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Transport from './transport';
import Driver from './driver';
import Product from './product';
import Trip from './trip';
import ImportProd from './import-prod';
import ExportProd from './export-prod';
import Grade from './grade';
import Statement from './statement';
import StatementType from './statement-type';
import Positioning from './positioning';
import ProductUnit from './product-unit';
/* jhipster-needle-add-route-import - JHipster will add routes here */

export default () => {
  return (
    <div>
      <ErrorBoundaryRoutes>
        {/* prettier-ignore */}
        <Route path="transport/*" element={<Transport />} />
        <Route path="driver/*" element={<Driver />} />
        <Route path="product/*" element={<Product />} />
        <Route path="trip/*" element={<Trip />} />
        <Route path="import-prod/*" element={<ImportProd />} />
        <Route path="export-prod/*" element={<ExportProd />} />
        <Route path="grade/*" element={<Grade />} />
        <Route path="statement/*" element={<Statement />} />
        <Route path="statement-type/*" element={<StatementType />} />
        <Route path="positioning/*" element={<Positioning />} />
        <Route path="product-unit/*" element={<ProductUnit />} />
        {/* jhipster-needle-add-route-path - JHipster will add routes here */}
      </ErrorBoundaryRoutes>
    </div>
  );
};
