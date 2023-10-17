import logging

from django.utils.deprecation import MiddlewareMixin

logger = logging.getLogger(__name__)

class LogErrorsMiddleware(MiddlewareMixin):
    def process_response(self, request, response):
        # If the response status code is 400 or above
        if response.status_code >= 400:
            # Log the entire response
            # This can be adjusted to log specific details depending on your needs
            logger.error(
                "Error response on %s: [%s] %s",
                request.path,
                response.status_code,
                response.content,
            )
        return response
