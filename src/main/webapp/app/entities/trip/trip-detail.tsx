import React, { useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { getEntity } from './trip.reducer';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const TripDetail = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  useEffect(() => {
    dispatch(getEntity(props.match.params.id));
  }, []);

  const tripEntity = useAppSelector(state => state.trip.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="tripDetailsHeading">
          <Translate contentKey="accountingImportExportProductsApp.trip.detail.title">Trip</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{tripEntity.id}</dd>
          <dt>
            <span id="distance">
              <Translate contentKey="accountingImportExportProductsApp.trip.distance">Distance</Translate>
            </span>
          </dt>
          <dd>{tripEntity.distance}</dd>
          <dt>
            <Translate contentKey="accountingImportExportProductsApp.trip.transport">Transport</Translate>
          </dt>
          <dd>{tripEntity.transport ? tripEntity.transport.id : ''}</dd>
          <dt>
            <Translate contentKey="accountingImportExportProductsApp.trip.driver">Driver</Translate>
          </dt>
          <dd>{tripEntity.driver ? tripEntity.driver.id : ''}</dd>
          <dt>
            <Translate contentKey="accountingImportExportProductsApp.trip.address">Address</Translate>
          </dt>
          <dd>{tripEntity.address ? tripEntity.address.id : ''}</dd>
          <dt>
            <Translate contentKey="accountingImportExportProductsApp.trip.product">Product</Translate>
          </dt>
          <dd>{tripEntity.product ? tripEntity.product.id : ''}</dd>
          <dt>
            <Translate contentKey="accountingImportExportProductsApp.trip.user">User</Translate>
          </dt>
          <dd>{tripEntity.user ? tripEntity.user.login : ''}</dd>
        </dl>
        <Button tag={Link} to="/trip" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/trip/${tripEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default TripDetail;
