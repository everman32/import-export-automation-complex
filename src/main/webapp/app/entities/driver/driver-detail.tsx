import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './driver.reducer';

export const DriverDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const driverEntity = useAppSelector(state => state.driver.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="driverDetailsHeading">
          <Translate contentKey="accountingImportExportProductsApp.driver.detail.title">Driver</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{driverEntity.id}</dd>
          <dt>
            <span id="firstname">
              <Translate contentKey="accountingImportExportProductsApp.driver.firstname">Firstname</Translate>
            </span>
          </dt>
          <dd>{driverEntity.firstname}</dd>
          <dt>
            <span id="patronymic">
              <Translate contentKey="accountingImportExportProductsApp.driver.patronymic">Patronymic</Translate>
            </span>
          </dt>
          <dd>{driverEntity.patronymic}</dd>
          <dt>
            <span id="lastname">
              <Translate contentKey="accountingImportExportProductsApp.driver.lastname">Lastname</Translate>
            </span>
          </dt>
          <dd>{driverEntity.lastname}</dd>
          <dt>
            <span id="phone">
              <Translate contentKey="accountingImportExportProductsApp.driver.phone">Phone</Translate>
            </span>
          </dt>
          <dd>{driverEntity.phone}</dd>
          <dt>
            <span id="experience">
              <Translate contentKey="accountingImportExportProductsApp.driver.experience">Experience</Translate>
            </span>
          </dt>
          <dd>{driverEntity.experience}</dd>
        </dl>
        <Button tag={Link} to="/driver" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/driver/${driverEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default DriverDetail;
