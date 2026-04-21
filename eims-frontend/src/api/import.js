import request from './request'

export const importApi = {
  importMaterials(file) {
    const formData = new FormData()
    formData.append('file', file)
    return request.post('/import/materials', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
  },
  importSuppliers(file) {
    const formData = new FormData()
    formData.append('file', file)
    return request.post('/import/suppliers', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
  },
  importRequirements(file) {
    const formData = new FormData()
    formData.append('file', file)
    return request.post('/import/requirements', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
  }
}
