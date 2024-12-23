import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getTrips } from 'app/entities/trip/trip.reducer';
import { getEntities as getGrades } from 'app/entities/grade/grade.reducer';
import { createEntity, getEntity, reset, updateEntity } from './import-prod.reducer';

export const ImportProdUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const trips = useAppSelector(state => state.trip.entities);
  const grades = useAppSelector(state => state.grade.entities);
  const importProdEntity = useAppSelector(state => state.importProd.entity);
  const loading = useAppSelector(state => state.importProd.loading);
  const updating = useAppSelector(state => state.importProd.updating);
  const updateSuccess = useAppSelector(state => state.importProd.updateSuccess);

  const handleClose = () => {
    navigate(`/import-prod${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getTrips({}));
    dispatch(getGrades({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    if (values.id !== undefined && typeof values.id !== 'number') {
      values.id = Number(values.id);
    }
    values.arrivalDate = convertDateTimeToServer(values.arrivalDate);

    const entity = {
      ...importProdEntity,
      ...values,
      trip: trips.find(it => it.id.toString() === values.trip?.toString()),
      grade: grades.find(it => it.id.toString() === values.grade?.toString()),
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {
          arrivalDate: displayDefaultDateTime(),
        }
      : {
          ...importProdEntity,
          arrivalDate: convertDateTimeFromServer(importProdEntity.arrivalDate),
          trip: importProdEntity?.trip?.id,
          grade: importProdEntity?.grade?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="accountingImportExportProductsApp.importProd.home.createOrEditLabel" data-cy="ImportProdCreateUpdateHeading">
            <Translate contentKey="accountingImportExportProductsApp.importProd.home.createOrEditLabel">
              Create or edit a ImportProd
            </Translate>
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? (
                <ValidatedField
                  name="id"
                  required
                  readOnly
                  id="import-prod-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                id="import-prod-trip"
                name="trip"
                data-cy="trip"
                label={translate('accountingImportExportProductsApp.importProd.trip')}
                type="select"
              >
                <option value="" key="0" />
                {trips
                  ? trips.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField
                label={translate('accountingImportExportProductsApp.importProd.arrivalDate')}
                id="import-prod-arrivalDate"
                name="arrivalDate"
                data-cy="arrivalDate"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                id="import-prod-grade"
                name="grade"
                data-cy="grade"
                label={translate('accountingImportExportProductsApp.importProd.grade')}
                type="select"
              >
                <option value="" key="0" />
                {grades
                  ? grades.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.description}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/import-prod" replace color="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">
                  <Translate contentKey="entity.action.back">Back</Translate>
                </span>
              </Button>
              &nbsp;
              <Button color="primary" id="save-entity" data-cy="entityCreateSaveButton" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp;
                <Translate contentKey="entity.action.save">Save</Translate>
              </Button>
            </ValidatedForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

export default ImportProdUpdate;
