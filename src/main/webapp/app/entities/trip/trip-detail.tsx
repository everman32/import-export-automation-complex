import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './trip.reducer';

export const TripDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
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
            <span id="authorizedCapital">
              <Translate contentKey="accountingImportExportProductsApp.trip.authorizedCapital">Authorized Capital</Translate>
            </span>
          </dt>
          <dd>{tripEntity.authorizedCapital}</dd>
          <dt>
            <span id="threshold">
              <Translate contentKey="accountingImportExportProductsApp.trip.threshold">Threshold</Translate>
            </span>
          </dt>
          <dd>{tripEntity.threshold}</dd>
          <dt>
            <Translate contentKey="accountingImportExportProductsApp.trip.user">User</Translate>
          </dt>
          <dd>{tripEntity.user ? tripEntity.user.login : ''}</dd>
          <dt>
            <Translate contentKey="accountingImportExportProductsApp.trip.transport">Transport</Translate>
          </dt>
          <dd>{tripEntity.transport ? tripEntity.transport.id : ''}</dd>
          <dt>
            <Translate contentKey="accountingImportExportProductsApp.trip.driver">Driver</Translate>
          </dt>
          <dd>{tripEntity.driver ? tripEntity.driver.id : ''}</dd>
          <dt>
            <Translate contentKey="accountingImportExportProductsApp.trip.hubPositioning">Hub Positioning</Translate>
          </dt>
          <dd>{tripEntity.hubPositioning ? tripEntity.hubPositioning.id : ''}</dd>
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
